import os
import websockets
import json
import asyncio
import requests

def requestDummyData():
    url = 'http://localhost:8090/mock/dummy'
    response = requests.post(url)
    print(response.text)

async def send_json_from_file(dummy_data):
    async with websockets.connect("ws://localhost:8090/_internal") as ws:
        while True:
            for data in dummy_data:
                try:
                    await ws.send(json.dumps(data))
                    await asyncio.sleep(0.1)
                except Exception as e:
                    print(e)
                    print('Connection closed')


def main(args=None):
    requestDummyData()
    cur_path = os.path.dirname(os.path.abspath(__file__)) 
    file_path = os.path.join(cur_path, '../dummy/dummy_data.txt')
    with open(file_path, 'r') as file:
        dummy_data = [json.loads(line.strip()) for line in file]

    asyncio.run(send_json_from_file(dummy_data))

if __name__ == '__main__':
    main()
