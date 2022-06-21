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

package ohos.samples.dataability;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.hiviewdfx.HiLogLabel;
import org.teasoft.beex.harmony.BeeConfigInit;
import org.teasoft.beex.harmony.ContextRegistry;
import org.teasoft.beex.harmony.RdbOpenCallbackRegistry;

/**
 * FileDataAbility
 */
public class UserDataAbility extends Ability {
    private static final String TAG = UserDataAbility.class.getSimpleName();
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD000F00, TAG);
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        BeeConfigInit.init(); //若有自定义的配置在bee.properties则需要
        ContextRegistry.register(this.getApplicationContext()); //将上下文注册到Bee
        RdbOpenCallbackRegistry.register(new MyRdbOpenCallback()); //将创建表和更新表的回调类,注册到Bee
//      BeeRdbStoreRegistry.register(rdbStore);  //直接注册rdbStore对象也可以.  但需要自己去生成,配置信息也不好管理
    }
}