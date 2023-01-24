# pet-service

```shell
# CREATE
curl -H "Content-Type: application/json" -XPOST http://localhost:8118/pets -d '{"name" : "Trixie", "type" : "Golden Retriever"}'
```

```shell
# READ
curl -XGET http://localhost:8118/pets/63cf02c7bed9a91c3ea9f828
```

```shell
# UPDATE
curl -H "Content-Type: application/json" -XPUT http://localhost:8118/pets/63cf02c7bed9a91c3ea9f828 -d '{"name" : "Trixie", "type" : "Golden Doodle"}'
```

```shell
# DELETE
curl  -H "Content-Type: application/json" -XDELETE http://localhost:8118/pets/63cf02c7bed9a91c3ea9f828
```
