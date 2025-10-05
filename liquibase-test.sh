
#-v ${{ github.workspace }}:/liquibase/changelog
docker run --rm \
  --network host \
  -v "$PWD":/liquibase/changelog \
  liquibase/liquibase:latest \
  --changeLogFile=changelog/src/main/resources/db/changelog/db.changelog-master.yaml \
  --url="jdbc:postgresql://localhost:5432/myfintechdb" \
  --username=myfintech \
  --password=myfintechpass \
  update
