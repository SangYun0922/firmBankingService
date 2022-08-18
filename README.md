# firmBankingService


# Admin Web Console
## URL
http://localhost:4000
## Installation
```
cd FirmBankingAPI_admin
npm install
```
## Start
```
cd FirmBankingAPI_admin
npm start
```
## Caution
- EurekaServer, API-Gateway, FirmBankingAPI_crud 모두 사전 실행되어있어야 한다.

# Prometheus/Grafana
## Prometheus
### URL
http://localhost:9090
### Start
```
cd Prometheus
prometheus --config.file=prometheus.yml 
```

## Grafana
### URL
http://localhost:3000
### Start
```
cd Grafana
./bin/grafana-server web
```
