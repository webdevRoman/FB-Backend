# Swagger code generator

https://github.com/swagger-api/swagger-codegen

`java -jar spec/swagger-codegen-cli.jar generate --help`

`java -jar spec/swagger-codegen-cli.jar config-help -l spring`

```
java -jar spec/swagger-codegen-cli.jar generate ^
-l spring ^
-i spec/swagger.yml ^
-o spec/generated ^
-c spec/swagger-codegen-config.json
```