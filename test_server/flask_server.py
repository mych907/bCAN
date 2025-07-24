from flask import Flask, request, jsonify

app = Flask(__name__)

# A simple in-memory store for the last received CAN data.
# In a real application, you would use a database or a more robust caching mechanism.
latest_can_data = {
    "id": "none",
    "speed": 0,
    "rpm": 0
}

@app.route('/can', methods=['POST'])
def receive_can_data():
    """
    Receives CAN data via POST request, prints it, and stores it.
    """
    global latest_can_data
    if request.is_json:
        data = request.get_json()
        print(f"Received CAN data: {data}")
        latest_can_data = data  # Update the stored data
        return jsonify({"status": "success", "data_received": data}), 200
    else:
        return jsonify({"status": "error", "message": "Request must be JSON"}), 400
    
@app.route('/can', methods=['GET'])
def get_can_data():
    """
    Returns the last received CAN data.
    """
    return jsonify(latest_can_data)

if __name__ == '__main__':
    # Runs the server on localhost, port 8080
    app.run(host='0.0.0.0', port=8080) 