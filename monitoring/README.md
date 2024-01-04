# Getting Started

### Reference Documentation

This project contains a Docker Compose file named `compose.yaml`.
In this file, the following services have been defined:
* co2monitoringapp (main service)
* rabbitmq: [`rabbitmq:latest`](https://hub.docker.com/_/rabbitmq)
* db (postgres database)

* Postgres database
```sh
# Create and start the database
docker compose build -d db
```

* Running the project locally

```sh
#Create the docker image
docker compose build
```

```sh
#Create the docker image
docker compose up co2monitoringapp
```

* This project includes 3 sensors for testing purpose, you can use the following sensor ids:
  * aaecf77c-93ca-492d-a486-2c4082699882
  * 738b9b40-931e-41a9-8944-04704c7846dd
  * 53edc6d9-6e9d-46d9-a249-7486a90dc74a
