import json
import time
import os
import pika
import sys
from config import device_id

# Deschide fișierul `sensor.csv`
try:
    f = open("sensor.csv", "r")
except FileNotFoundError:
    print("Fișierul sensor.csv nu există. Verifică dacă este în același director cu scriptul.")
    sys.exit(1)

prev = 0

# Configurarea conexiunii la RabbitMQ
url = os.environ.get(
    'CLOUDAMQP_URL',
    'amqps://vilnylfk:MJHdgeSXnpbv5Zu--BrA0Q5HxIef0moH@cow.rmq2.cloudamqp.com/vilnylfk'
)
params = pika.URLParameters(url)

try:
    connection = pika.BlockingConnection(params)
    channel = connection.channel()  # Start a channel
    channel.queue_declare(queue='masuratori')  # Declare a queue
    print("Conectat la RabbitMQ și coada 'masuratori' a fost creată.")
except pika.exceptions.AMQPConnectionError as e:
    print(f"Eroare la conectare RabbitMQ: {e}")
    sys.exit(1)

# Trimite datele din `sensor.csv` către RabbitMQ
try:
    for t in f:
        try:
            t = float(t.strip())
            element = {
                "timestamp": int(time.time()),
                "device_id": device_id,  
                "measurement_value": t # - prev
            }
            channel.basic_publish(
                exchange='',
                routing_key='masuratori',
                body=json.dumps(element, indent=4)
            )
            prev = t
            print(f"Trimis: {element}")
            time.sleep(1)  
        except ValueError:
            print(f"Ignor linia invalidă: {t.strip()}")

except Exception as e:
    print(f"Eroare la trimiterea mesajelor: {e}")
finally:
    connection.close()
    f.close()
    print("Conexiunea RabbitMQ și fișierul au fost închise corect.")

print('Toate datele au fost trimise.')
