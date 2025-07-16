# BCDonaciones – Donation Platform on Hyperledger Fabric

**Tech stack:** Hyperledger Fabric · Go chain-code · Java / Spring Boot API · Angular · Docker

## Quick Local Deployment

```bash
# 1. Clone
git clone --recurse-submodules https://github.com/r4f4777/BCDonaciones.git
cd BCDonaciones

# 2. Launch Fabric test network
cd ~/fabric-samples/test-network
./network.sh up createChannel -ca
./network.sh deployCC -ccn donation -ccp ../chaincode/donation -ccl go

# 3. Backend + DB
cd ~/BCDonaciones
docker-compose up --build -d      # MariaDB + Spring Boot

# 4. Front-end (Angular)
cd frontend
npm install
ng serve

App runs at http://localhost:4200

## Project Structure
backend/   # Spring Boot
frontend/  # Angular
chaincode/donation/  # Go smart-contract


