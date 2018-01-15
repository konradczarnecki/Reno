package konra.reno.core.persistance;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RenoRepository {

    MongoTemplate template;

    @Autowired
    public RenoRepository(MongoTemplate template) {
        this.template = template;
    }

    public String getCollectionHash(String name) {

        DBObject command = new BasicDBObject("dbHash", 1);
        CommandResult res = template.getDb().command(command);
        return (String) res.get("collections." + name);
    }
}
