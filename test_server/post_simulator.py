import requests
import json
import time
import random

def post_can_data():
    """
    Posts simulated CAN data to a local server.
    """
    # The URL of the server endpoint.
    # You might need to change the port (e.g., 8080) to match your Android app's server.
    url = "http://127.0.0.1:8080/can"
    
    while True:
        try:
            # Simulate some CAN data
            can_data = {
                "id": "can0",
                "speed": random.randint(40, 120),  # Simulated speed in km/h
                "rpm": random.randint(1500, 4000)  # Simulated engine RPM
            }

            # Send the data as a POST request with JSON payload
            response = requests.post(url, json=can_data)

            # Check the response from the server
            if response.status_code == 200:
                print(f"Successfully posted data: {can_data}")
            else:
                print(f"Failed to post data. Status code: {response.status_code}, Response: {response.text}")

        except requests.exceptions.RequestException as e:
            print(f"Error connecting to the server: {e}")

        # Wait for a second before sending the next data
        time.sleep(1)

if __name__ == "__main__":
    post_can_data()
