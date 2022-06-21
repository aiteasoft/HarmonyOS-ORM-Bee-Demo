/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ohos.samples.dataability.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Text;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.samples.dataability.ResourceTable;
import ohos.samples.dataability.bee.BeeTest;
import ohos.samples.dataability.entity.Person;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.FunctionType;
import org.teasoft.bee.osql.Suid;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.shortcut.BF;

import java.security.SecureRandom;
import java.util.List;

/**
 * MainAbilitySlice
 */
public class MainAbilitySlice extends AbilitySlice {
    private static final String TAG = MainAbilitySlice.class.getSimpleName();

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD000F00, TAG);

    private Text logText;

    Suid suid = BF.getSuid();   //简单的select,update,insert,delete操作
    SuidRich suidRich = BF.getSuidRich(); //丰富多样的suid操作
     //BF是BeeFactoryHelper的简称,也可以如下用法:
    //Suid suid=BeeFactoryHelper.getSuid();

    @Override
    public void onStart(Intent intent) {
        HiLog.info(LABEL_LOG, "------------in MainAbilitySlice onStart!");
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_main_ability_slice);
        initComponents();
    }

    private void initComponents() {
        Component insertButton = findComponentById(ResourceTable.Id_insert_button);
        insertButton.setClickedListener(this::insert);
        Component deleteButton = findComponentById(ResourceTable.Id_delete_button);
        deleteButton.setClickedListener(this::delete);
        Component updateButton = findComponentById(ResourceTable.Id_update_button);
        updateButton.setClickedListener(this::update);
        Component queryButton = findComponentById(ResourceTable.Id_query_button);
        queryButton.setClickedListener(component -> query(false));
        Component batchInsertButton = findComponentById(ResourceTable.Id_batch_insert_button);
        batchInsertButton.setClickedListener(this::batchInsert);
        Component batchExecuteButton = findComponentById(ResourceTable.Id_batch_execute_button);
        batchExecuteButton.setClickedListener(this::batchExecute);
        Component homeButton = findComponentById(ResourceTable.Id_read_file_button);
        homeButton.setClickedListener(this::toHomeReadMe);
        homeReadMe();
    }

    private void homeReadMe(){
        logText = (Text) findComponentById(ResourceTable.Id_log_text);
        StringBuilder appendStr = new StringBuilder();
        appendStr.append("                     [各按钮演示说明]" + System.lineSeparator());
        appendStr.append("[Insert ]: 插入一条记录,然后显示所有记录;" + System.lineSeparator());
        appendStr.append("[Delete ]: 删除第1和第2条记录" + System.lineSeparator());
        appendStr.append("[Update ]: 修改第一条记录;" + System.lineSeparator());
        appendStr.append("[Query  ]: 查询第2条开始的一页数据(5条)" + System.lineSeparator());
        appendStr.append("[性能测试 ]: 测试操作1万条数据的各项性能." + System.lineSeparator());
        logText.setText(appendStr.toString());
    }

    private void insert(Component component) {
        HiLog.info(LABEL_LOG, "----------------insert");
        try {
            Person p = new Person();
            p.setName(getRandomName());
            p.setAge(getRandomAge());
            suid.insert(p);
            HiLog.info(LABEL_LOG, "----------------insert结束.");
        } catch (Exception e) {
            HiLog.error(LABEL_LOG, "--------------insert--:" + e.getMessage());
        }
        query(true);
    }

    private void delete(Component component) {
        HiLog.info(LABEL_LOG, "----------------delete");

        try {
            Condition condition = BF.getCondition();
            condition.between("userId", 1, 2);
            suid.delete(new Person(), condition);
        } catch (Exception e) {
            HiLog.error(LABEL_LOG, "--------------insert--:" + e.getMessage());
        }
        query(true);
    }

    private void update(Component component) {
        HiLog.info(LABEL_LOG, "----------------update");
        try {
            Person p = new Person();
            p.setName("Tom_update");
            p.setAge(0);
            p.setUserId(1);
            suid.update(p); //根据id修改对象
        } catch (Exception exception) {
            HiLog.error(LABEL_LOG, "%{public}s", "update: dataRemote exception|illegalStateException");
        }
        query(true);
    }

    private void query(boolean queryAll) {
        HiLog.info(LABEL_LOG, "----------------query");

        getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
            List<Person> list = null;
            if (queryAll) {  //查所有
                list = suid.select(new Person());
            }else {
                list = suidRich.select(new Person(), 2, 5); //查从第2条开始的5条数据
            }
            appendText(list);
        });
    }

    private void batchExecute(Component component) {
        HiLog.info(LABEL_LOG, "----------------batchExecute");

        getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
            testFun();
            testMost();
        });
     }

    private void appendText(List<Person> list) {
        if (list == null || list.size() < 1) return;

        StringBuilder appendStr = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Person p = list.get(i);

            String name = p.getName();
            int age = p.getAge();
            int userId = p.getUserId();
            appendStr.append(userId).append("   ").append(name).append("   ").append(age).append(System.lineSeparator());
        }

        getUITaskDispatcher().asyncDispatch(() -> {
            logText.setText("");
            logText.setText(appendStr.toString());
        });
    }

    private void batchInsert(Component component) {
        HiLog.info(LABEL_LOG, "----------------batchInsert");
        Person array[] = new Person[2];
        Person p0 = new Person();
        p0.setName(getRandomName());
        p0.setAge(getRandomAge());

        Person p1 = new Person();
        p1.setName(getRandomName());
        p1.setAge(getRandomAge());

        array[0] = p0;
        array[1] = p1;
        suidRich.insert(array);
        query(true);
    }

    private void toHomeReadMe(Component component) {
        homeReadMe();
    }

    // test data
    private static int num=1;
    private int getRandomAge() {

        //测试 1/3 返回的结果:    0.333333333333333
        if(num==1) {
            num++; return 1;
        }
        if(num==2 || num==3){
            num++; return 0;
        }

        return new SecureRandom().nextInt(20);
    }

    // test data
    private String getRandomName() {
        String[] names = {"Tom", "Jerry", "Bob", "Coco", "Sum", "Marry"};
        int index = new SecureRandom().nextInt(names.length);
        return names[index];
    }

    private void testFun(){
        String max=suidRich.selectWithFun(new Person(), FunctionType.MAX, "age");
        String min=suidRich.selectWithFun(new Person(), FunctionType.MIN, "age");
        String sum=suidRich.selectWithFun(new Person(), FunctionType.SUM, "age");
        String avg=suidRich.selectWithFun(new Person(), FunctionType.AVG, "age");
        String count=suidRich.selectWithFun(new Person(), FunctionType.COUNT, "age");

        System.out.println("----------------max:"+max+",min:"+min+",sum:"+sum+",avg:"+avg+",count:"+count+".");
    }

    private static final String prefix="----------------";
    private void testMost() {
        System.out.println("----------------测试开始--");
        BeeTest beeTest = new BeeTest();
//		beeTest.testCreateTable();
		beeTest.testInsertAndReturn();

		beeTest.testMoretable();
		beeTest.testBatchInsert();
//		beeTest.testFun(); //加测输出结果

        beeTest.testSelectSome();
		beeTest.testTran();

		beeTest.testRollback(false);
		beeTest.testRollback(true);
        beeTest.testSelectSome();

        beeTest.testProblem();//SQL 注入测试


        //操作1万条数据,性能测试
        beeTest.testDelete10000();

        long t1 = System.currentTimeMillis();
        int a = beeTest.testBatchInsert10000();
        long t2 = System.currentTimeMillis();
        long  need1=(t2 - t1);
        System.out.println(prefix+"插入1w使用时间:" + need1);
        System.out.println(prefix+"插入的数据条数: " + a);


        long s1 = System.currentTimeMillis();
        beeTest.testQuery10000();
        long s2 = System.currentTimeMillis();
        long  need2=(s2 - s1);
        System.out.println(prefix+"查询1w使用时间:" + need2);
        System.out.println(prefix+"查询的数据条数: " + 10000);

        long d1 = System.currentTimeMillis();
        int d = beeTest.testDelete10000();
        long d2 = System.currentTimeMillis();
        long  need3=(d2 - d1);
        System.out.println(prefix+"删除1w使用时间:" + need3);
        System.out.println(prefix+"删除的数据条数: " + d);

        long t3 = System.currentTimeMillis();
        int b = beeTest.testBatchInsert10000Div2();
        long t4 = System.currentTimeMillis();
        long  need4=(t4 - t3);
        System.out.println(prefix+"插入1w(分2批)使用时间:" + need4);
        System.out.println(prefix+"插入的数据条数: " + b);

        beeTest.testDelete10000();

        StringBuilder appendStr = new StringBuilder();
        appendStr.append(System.lineSeparator());
        appendStr.append("插入1w使用时间:" + need1).append(System.lineSeparator());
        appendStr.append("查询1w使用时间:" + need2).append(System.lineSeparator());
        appendStr.append("删除1w使用时间:" + need3).append(System.lineSeparator());
        appendStr.append("插入1w(分2批)使用时间:" + need4).append(System.lineSeparator());


        getUITaskDispatcher().asyncDispatch(() -> {
            logText.setText("");
            logText.setText(appendStr.toString());
        });
        System.out.println("----------------测试结束--");
    }

}
