# Image Repository API

This API:
- Uploads a CSV with columns: `name,size,address`
- Batch-inserts records into Postgres using **Spring Batch**
- Exposes a REST API to page/filter results
- Displays the data in a UI with pagination
- Runs as **three services** via `docker compose` (frontend, backend, postgres)

```bash
docker compose build
docker compose up
```
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Postgres: localhost:5432 (user: `app`, password: `app`, db: `appdb`)

### CSV format


### Upload flow


## Stack details

