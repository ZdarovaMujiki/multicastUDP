# multicastUDP
Application that detects copies of itself on the local network using the exchange of multicast UDP messages

## Usage

### Build
```bash
./gradlew jar
```

### Run
```bash
java -jar build/libs/multicasUDP.jar [multicast group ip] [port]
```