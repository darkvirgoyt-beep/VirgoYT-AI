# Railway findings

- Railway account is logged in as VirgoYT.
- Dashboard shows a trial with 29 days or $5.00 remaining.
- Existing projects: `modest-consideration` with 3/4 services online and `affectionate-determination` with 1/1 service online.
- Official Railway docs confirm persistent services can run containerized applications, Dockerfiles are supported, and volumes provide persistent data.
- Railway volume defaults: Free/Trial 0.5 GB, Hobby 5 GB, Pro 50 GB. A full desktop session needs careful resource sizing.
- Railway services have ephemeral filesystems outside volumes and can be redeployed/migrated.
- The current VirgoYT-AI repository has no Railway config and no Railway CLI installed.
- The repository already has `Dockerfile` and `Dockerfile.web`, but neither provides a graphical Linux desktop or desktop streaming service.
- A full desktop implementation will require an additional desktop container/service, browser-streaming endpoint, secure access token, and workspace iframe/control integration.
- Do not expose an unauthenticated desktop or database publicly; use authentication and Railway variables/volumes.

Sources:
- https://docs.railway.com/volumes/reference
- https://docs.railway.com/services
- https://docs.railway.com/deployments/reference

Decision: Railway is technically suitable for a lightweight Dockerized desktop service, but the current trial has limited remaining usage and a fallback to a Standard cloud server may be needed for a reliable full desktop workload.
