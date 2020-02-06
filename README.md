## Assessment

### Setup

####Pull image from docker hub
```bash
docker pull retkala/assessment:latest
```

####Run docker image
```bash
docker run -p 8080:8080 -t retkala/assessment
```

#####Example request

```$xslt
curl -i -X POST \
   -H "Content-Type:application/json" \
   -d \
'{
  "url": "https://s3-eu-west-1.amazonaws.com/merapar-assessment/arabic-posts.xml"
}' \
 'http://localhost:8080/analyze/'

```
#### Pushing the image to docker hub
1. Login to your docker hub
2. ```./pushDockerHub.sh yourDockerName```

