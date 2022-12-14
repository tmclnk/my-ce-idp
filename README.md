# Spring Boot Custom Identity Provider

Dummy IdP to verify we can hack our legacy authentication into a modern
auth flow using CloudEntity.

![login screen](./docs/login.png)

## Configuration

### application.properties
You'll need to set these values in `src/main/resources/application.properties` (which is .gitignore'd).

```properties
# cloudentity.issuer-uri=https://{{tid}}.us.authz.cloudentity.io/api/system/{{tid}}
cloudentity.issuer-uri=
# cloudentity.auth-server=https://{{tid}}.us.authz.cloudentity.io/{{tid}}/{{wsid}}
cloudentity.auth-server=
cloudentity.client-id=
cloudentity.client-secret=
```

### Login URL

Use `http://localhost:8888/login` as the login url in the CE IdP configuration.

## Running

We're expecting Java 17.

```shell
./mvnw spring-boot:run
```

You'll need a client app to perform a redirect to get you valid
`login_id` and `login_state` values on the login page. The default demo app 
in the workspace should suffice.


## Links

- [HOWTO Create Custom IDP in CloudEntity](https://cloudentity.com/developers/howtos/identities/custom-idp/)