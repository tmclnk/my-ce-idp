# Spring Boot Custom Identity Provider

Dummy IdP to verify we can hack our legacy authentication into a modern
auth flow using CloudEntity.

![login screen](./docs/login.png)

## Configuration

### Register Custom IdP in ACP

You'll need to add an **Identity Provider** in ACP. This will be a **Custom IDP**. When prompted, the Login URL will be

```text
http://localhost:8888/login
```

### application.properties

Create a `src/main/resources/application.properties` (which is .gitignore'd).

```properties
# Replace {{tid}} with your Tenant ID.
# Replace {{wsid}} with your Workspace Id, e.g. "demo".
cloudentity.issuer-uri=https://{{tid}}.us.authz.cloudentity.io/api/system/{{tid}}
cloudentity.auth-server=https://{{tid}}.us.authz.cloudentity.io/{{tid}}/{{wsid}}

# Use the client-id and client-secret from the Custom IdP
cloudentity.client-id=
cloudentity.client-secret=
```

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