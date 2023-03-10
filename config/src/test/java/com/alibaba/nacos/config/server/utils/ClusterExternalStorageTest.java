/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.config.server.utils;

import com.alibaba.nacos.config.server.constant.PropertiesConstant;
import com.alibaba.nacos.config.server.service.datasource.DynamicDataSource;
import com.alibaba.nacos.config.server.service.datasource.ExternalDataSourceServiceImpl;
import com.alibaba.nacos.config.server.service.datasource.LocalDataSourceServiceImpl;
import com.alibaba.nacos.sys.env.Constants;
import com.alibaba.nacos.sys.env.EnvUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * ClusterExternalStorage unit test.
 *
 * @author Long Yu
 * @since 2.2.0
 */
@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClusterExternalStorageTest {
    
    @InjectMocks
    private DynamicDataSource dataSource;
    
    private MockEnvironment environment;
    
    @Mock
    private LocalDataSourceServiceImpl localDataSourceService;
    
    @Mock
    private ExternalDataSourceServiceImpl basicDataSourceService;
    
    PropertyUtil propertyUtil = new PropertyUtil();
    
    @Before
    public void setUp() throws Exception {
        environment = new MockEnvironment();
        EnvUtil.setEnvironment(environment);
        dataSource = DynamicDataSource.getInstance();
        ReflectionTestUtils.setField(dataSource, "localDataSourceService", localDataSourceService);
        ReflectionTestUtils.setField(dataSource, "basicDataSourceService", basicDataSourceService);
       
    }
    
    @Test
    public void test005WithClusterAndNullDatabase() {
        // ??????????????????05???????????????????????????????????????UseExternalDB???true?????????????????????""
        System.setProperty(Constants.STANDALONE_MODE_PROPERTY_NAME, "false");
        environment.setProperty(PropertiesConstant.DATASOURCE_PLATFORM_PROPERTY_OLD, "");
        EnvUtil.setIsStandalone(Boolean.getBoolean(Constants.STANDALONE_MODE_PROPERTY_NAME));
        PropertyUtil.setEmbeddedStorage(EnvUtil.getStandaloneMode());
        
        // ???????????????
        propertyUtil.initialize(null);

        Assert.assertFalse(EnvUtil.getStandaloneMode());
        Assert.assertTrue(PropertyUtil.isUseExternalDB());
        Assert.assertTrue(dataSource.getDataSource() instanceof ExternalDataSourceServiceImpl);
    }

    @Test
    public void test006WithClusterAndMysqlDatabase() {
        // ??????????????????06?????????????????????????????????mysql???UseExternalDB???true?????????????????????mysql
        System.setProperty(Constants.STANDALONE_MODE_PROPERTY_NAME, "false");
        environment.setProperty(PropertiesConstant.DATASOURCE_PLATFORM_PROPERTY_OLD, "mysql");
        EnvUtil.setIsStandalone(Boolean.getBoolean(Constants.STANDALONE_MODE_PROPERTY_NAME));
        PropertyUtil.setEmbeddedStorage(EnvUtil.getStandaloneMode());
    
        // ???????????????
        propertyUtil.initialize(null);

        Assert.assertFalse(EnvUtil.getStandaloneMode());
        Assert.assertTrue(PropertyUtil.isUseExternalDB());
        Assert.assertTrue(dataSource.getDataSource() instanceof ExternalDataSourceServiceImpl);
    }

    @Test
    public void test007WithClusterAndDerbyDatabase() {
        // ??????????????????07?????????????????????????????????derby???UseExternalDB???false?????????????????????derby
        System.setProperty(Constants.STANDALONE_MODE_PROPERTY_NAME, "false");
        environment.setProperty(PropertiesConstant.DATASOURCE_PLATFORM_PROPERTY_OLD, "derby");
        EnvUtil.setIsStandalone(Boolean.getBoolean(Constants.STANDALONE_MODE_PROPERTY_NAME));
        PropertyUtil.setEmbeddedStorage(true);
        
        // ???????????????
        propertyUtil.initialize(null);

        Assert.assertFalse(EnvUtil.getStandaloneMode());
        Assert.assertFalse(PropertyUtil.isUseExternalDB());
        Assert.assertTrue(dataSource.getDataSource() instanceof LocalDataSourceServiceImpl);
    }

    @Test
    public void test008WithClusterAndOtherDatabase() {
        // ??????????????????08: ???????????????????????????????????????UseExternalDB???true???????????????????????????
        System.setProperty(Constants.STANDALONE_MODE_PROPERTY_NAME, "false");
        environment.setProperty(PropertiesConstant.DATASOURCE_PLATFORM_PROPERTY_OLD, "postgresql");
        EnvUtil.setIsStandalone(Boolean.getBoolean(Constants.STANDALONE_MODE_PROPERTY_NAME));
        PropertyUtil.setEmbeddedStorage(EnvUtil.getStandaloneMode());
        
        // ???????????????
        propertyUtil.initialize(null);

        Assert.assertFalse(EnvUtil.getStandaloneMode());
        Assert.assertTrue(PropertyUtil.isUseExternalDB());
        Assert.assertTrue(dataSource.getDataSource() instanceof ExternalDataSourceServiceImpl);
    }

}
