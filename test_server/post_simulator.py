import os
import time
import json
from typing import Any, Dict, Optional

import requests

try:
    import can
except ImportError as exc:  # pragma: no cover
    raise SystemExit(
        "python-can is required. Install with: pip install python-can"
    ) from exc

try:
    import cantools
except ImportError as exc:  # pragma: no cover
    raise SystemExit(
        "cantools is required. Install with: pip install cantools"
    ) from exc


DBC_PATH = os.environ.get(
    "DBC_PATH", os.path.join(os.path.dirname(__file__), "SKB_radar_1.dbc")
)

# Server endpoint (start local first)
SERVER_URL = os.environ.get("CAN_POST_URL", "https://bcan-server.onrender.com/data")

# Kvaser channel configuration
# NOTE: Kvaser channels are 0-indexed in python-can; channel 0 typically maps to the device's CAN1
KVASER_CHANNEL = int(os.environ.get("KVASER_CHANNEL", "0"))
BITRATE = int(os.environ.get("BITRATE", "500000"))
DATA_BITRATE = int(os.environ.get("DATA_BITRATE", "2000000"))
USE_CAN_FD = True

# Trigger CAN ID (default to 0x200 = 512). When this ID arrives, send grouped payload
TRIGGER_CAN_ID = int(os.environ.get("TRIGGER_CAN_ID", str(0x200)))


def load_database(dbc_path: str):
    if not os.path.exists(dbc_path):
        raise FileNotFoundError(f"DBC file not found: {dbc_path}")
    return cantools.database.load_file(dbc_path)


def open_kvaser_bus() -> can.Bus:
    # kvaser backend supports fd=True and data_bitrate for CAN-FD
    return can.Bus(
        interface="kvaser",
        channel=KVASER_CHANNEL,
        bitrate=BITRATE,
        data_bitrate=DATA_BITRATE,
        fd=USE_CAN_FD,
        receive_own_messages=False,
    )


def build_payload(
    msg: can.Message,
    db: Optional[cantools.database.can.Database] = None,
) -> Dict[str, Any]:
    name: Optional[str] = None
    signals: Optional[Dict[str, Any]] = None

    if db is not None:
        try:
            msg_def = db.get_message_by_frame_id(msg.arbitration_id)
            name = msg_def.name
            decoded = msg_def.decode(msg.data)
            if decoded is not None:
                signals = decoded
        except (KeyError, cantools.database.errors.Error, AttributeError, ValueError):
            signals = None

    payload: Dict[str, Any] = {
        "timestamp": msg.timestamp,
        "canId": msg.arbitration_id,
        "isExtendedId": bool(getattr(msg, "is_extended_id", False)),
        "dlc": msg.dlc,
        "isFd": bool(getattr(msg, "is_fd", USE_CAN_FD)),
        "name": name,
        "signals": signals,
        "raw": msg.data.hex(),
    }
    return payload


def post_payload(url: str, payload: Dict[str, Any]) -> None:
    try:
        resp = requests.post(url, json=payload, timeout=2.0)
        if resp.status_code != 200:
            print(f"POST failed {resp.status_code}: {resp.text[:200]}")
    except requests.RequestException as exc:
        print(f"POST error: {exc}")


def main() -> None:
    print(
        json.dumps(
            {
                "dbc": DBC_PATH,
                "server_url": SERVER_URL,
                "channel": KVASER_CHANNEL,
                "bitrate": BITRATE,
                "data_bitrate": DATA_BITRATE,
                "fd": USE_CAN_FD,
            },
            indent=2,
        )
    )

    db = load_database(DBC_PATH)
    frame_ids = {m.frame_id for m in db.messages}

    with open_kvaser_bus() as bus:
        print("Listening on Kvaser bus... (Ctrl+C to stop)")
        # Buffer that groups latest payload by canId
        grouped_by_can_id: Dict[int, Dict[str, Any]] = {}
        while True:
            try:
                msg = bus.recv(timeout=1.0)
            except can.CanError as exc:
                print(f"CAN receive error: {exc}")
                time.sleep(0.2)
                continue

            if msg is None:
                continue

            # Only forward messages defined in the DBC
            if msg.arbitration_id not in frame_ids:
                continue

            payload = build_payload(msg, db)

            # Update aggregation buffer
            can_id: int = payload.get("canId")  # type: ignore[assignment]
            rest_payload: Dict[str, Any] = {k: v for k, v in payload.items() if k != "canId"}
            grouped_by_can_id[can_id] = rest_payload

            # Send the grouped payload when trigger ID arrives
            if can_id == TRIGGER_CAN_ID:
                # Convert keys to strings for JSON consistency
                grouped_payload: Dict[str, Dict[str, Any]] = {str(k): v for k, v in grouped_by_can_id.items()}
                post_payload(SERVER_URL, grouped_payload)
                grouped_by_can_id.clear()


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\nStopped by user.")


