package ohos.samples.dataability;

import ohos.data.rdb.RdbOpenCallback;
import ohos.data.rdb.RdbStore;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.samples.dataability.bee.entity.*;
import ohos.samples.dataability.entity.Person;
import org.teasoft.honey.osql.autogen.Ddl;
import org.teasoft.honey.osql.core.HoneyContext;

public class MyRdbOpenCallback extends RdbOpenCallback {
    private static final String TAG = "MyRdbOpenCallback";
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD000F00, TAG);
    @Override
    public void onCreate(RdbStore store) {
        try{

//        store.executeSql(   //手动写sql
//                "create table if not exists " +  "person (user_id integer primary key autoincrement, "
//                        + "name text not null, "  + "age integer)");

            HiLog.info(LABEL_LOG,"--------------------创建表.......开始.");

            String sql= Ddl.toCreateTableSQL(new Person()); //不想写sql可以自动生成
            HiLog.info(LABEL_LOG, "---------------create table sql:"+sql);
            store.executeSql(sql);

            store.executeSql(Ddl.toCreateTableSQL(new LeafAlloc()));
            store.executeSql(Ddl.toCreateTableSQL(new Orders()));
            store.executeSql(Ddl.toCreateTableSQL(new Tb_inaccount()));
            store.executeSql(Ddl.toCreateTableSQL(new Tb_outaccount()));
            store.executeSql(Ddl.toCreateTableSQL(new TestUser()));
         } catch (Exception e) {
           HiLog.error(LABEL_LOG, "---------------create table:"+e.getMessage());
        }
        HiLog.info(LABEL_LOG, "------------onCreate  finished!");
    }

    @Override
    public void onUpgrade(RdbStore store, int oldVersion, int newVersion) {
        HoneyContext.setCurrentAppDB(store);
        HiLog.info(LABEL_LOG,"--------------------更新表.......");
        HiLog.info(LABEL_LOG, "%{public}s", "DataBase upgrade");
        HoneyContext.removeCurrentAppDB();
    }

}
