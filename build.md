
mvn clean install

docker build -t fhir-facade .

docker tag fhir-facade:latest 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-facade:latest
docker tag fhir-facade:latest 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-facade:1.0.1

docker push 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-facade:latest

docker push 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-facade:1.0.1
