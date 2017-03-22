package at.ac.fhsalzburg.repository;

import org.springframework.stereotype.Repository;

@Repository
public class UuidRepositoryImpl implements UuidRepository {

    @Override
    public String getUuid(String uuid) {
        if(uuid.equals("23a45cd535f")){
            return "{   \"width\": 400,   \"height\": 200,   \"padding\": {\"top\": 10, \"left\": 30, \"bottom\": 30, \"right\": 10}, \"dataSchema\":{\"fields\":[ {\"field\":\"A\", \"selector\":\"/foo/bar/baz/value()\", \"type\":\"java.lang.Integer\"}, {\"field\":\"B\", \"selector\":\"/foo/bar/value()\", \"type\":\"java.lang.Integer\"} ]}, \"data\": [     {       \"name\": \"table\"  }   ],   \"scales\": [     {       \"name\": \"x\",       \"type\": \"ordinal\",       \"range\": \"width\",       \"domain\": {\"data\": \"table\", \"field\": \"x\"}     },     {       \"name\": \"y\",       \"type\": \"linear\",       \"range\": \"height\",       \"domain\": {\"data\": \"table\", \"field\": \"y\"},       \"nice\": true     }   ],   \"axes\": [     {\"type\": \"x\", \"scale\": \"x\"},     {\"type\": \"y\", \"scale\": \"y\"}   ],   \"marks\": [     {       \"type\": \"rect\",       \"from\": {\"data\": \"table\"},       \"properties\": {         \"enter\": {           \"x\": {\"scale\": \"x\", \"field\": \"x\"},           \"width\": {\"scale\": \"x\", \"band\": true, \"offset\": -1},           \"y\": {\"scale\": \"y\", \"field\": \"y\"},           \"y2\": {\"scale\": \"y\", \"value\": 0}         },         \"update\": {           \"fill\": {\"value\": \"steelblue\"}         },         \"hover\": {           \"fill\": {\"value\": \"red\"}         }       }     }   ] }";
        }
        return "";
    }
}
