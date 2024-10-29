# api-publisher-test-api

This service will be used to test publish failures.

This can now be updated with the following curl afterwards it will respond with the relevant definition/OAS.
These are defined in resources/public/api/definitions

```bash
curl -X POST --location "http://localhost:XXX/set-definition" \
    -H "Content-Type: application/json" \
    -d '{
          "location": "v1_stable_v2_alpha.json"
        }'
```

| Scenario                                 | State                 | Filename                   |
|------------------------------------------|-----------------------|----------------------------|
| Failed publish no definition             | Not found             | notfound                   |
| Successful publish Alpha version         | v1 ALPHA PUBLIC       | v1_alpha.json              |
| Successful publish Beta version          | v1 BETA PUBLIC        | v1_beta.json               |
| Successful publish Private version       | v1 BETA PRIVATE       | v1_beta_private.json       |
| Successful publish Private trial version | v1 BETA PRIVATE trial | v1_beta_private_trial.json |
| Successful publish two versions          | v1 STABLE -> v2 ALPHA | v1_stable_v2_alpha.json    |


By default, the API will return resources/public/api/conf/1.0/application.yaml when asked to return the OAS for the API.
However, it will return an alternative OAS file (e.g. application2.yaml) if the following is run first:

```bash
curl -X POST --location "http://localhost:XXX/set-specification" \
    -H "Content-Type: application/json" \
    -d '{
          "location": "application2.yaml"
        }'
```

### License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
