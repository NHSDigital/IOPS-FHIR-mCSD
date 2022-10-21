
mvn clean install

docker build -t fhir-mcsd .

docker tag fhir-mcsd:latest 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-mcsd:latest
docker tag fhir-mcsd:latest 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-mcsd:1.0.4

docker push 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-mcsd:latest

docker push 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-mcsd:1.0.4
