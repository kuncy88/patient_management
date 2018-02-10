package hu.kuncystem.patient.dao;

import java.io.File;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * This is the settings of h2 database.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. dec. 1.
 * 
 * @version 1.0
 */
@Configuration
@ComponentScan(basePackages = { "hu.kuncystem.patient.dao" })
@EnableTransactionManagement
public class H2Config {
    // database name which we use in the real system
    private final static String DB_NAME = "~/PatientManagament/databases/h2main";
    // user of database
    private final static String DB_USER = "admin";
    // password of database
    private final static String DB_PASSWORD = "asdf1234";

    // This is the marker which indicate that the database is exists or not
    private boolean dbExists = true;

    /**
     * This method will run sql scripts on the database. For example: the
     * database is not exists and we are creating this now, and we will run this
     * method which will load the default data into the database.
     */
    private DatabasePopulator createDatabasePopulator() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setContinueOnError(true);
        databasePopulator.addScript(new ClassPathResource("schema.sql"));
        databasePopulator.addScript(new ClassPathResource("default_data.sql"));
        return databasePopulator;
    }

    /**
     * Create new data source. This source will use the database from the disk.
     */
    private SimpleDriverDataSource createDataSourceLiveH2OnDisk() {
        SimpleDriverDataSource simpleDriverDataSource = new SimpleDriverDataSource();
        simpleDriverDataSource.setDriverClass(org.h2.Driver.class);
        simpleDriverDataSource.setUsername(DB_USER);
        simpleDriverDataSource.setPassword(DB_PASSWORD);
        simpleDriverDataSource
                .setUrl("jdbc:h2:" + DB_NAME + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE");

        // check the databse file(exsits or not)
        File file = new File(DB_NAME.replace("~/", System.getProperty("user.home") + File.separator) + ".mv.db");
        dbExists = file.exists();

        return simpleDriverDataSource;
    }

    @Bean
    @Profile("live")
    public DataSource dataSourceLive() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("schema.sql").build();
    }

    @Bean
    @Profile("liveH2OnDisk")
    public DataSource dataSourceLiveH2OnDisk() {
        DataSource dataSource = createDataSourceLiveH2OnDisk();
        if (!dbExists) { // this is new databse, so we have to run extra sql
                         // script.
            DatabasePopulatorUtils.execute(createDatabasePopulator(), dataSource);
        }
        return dataSource;
    }

    @Bean
    @Profile("test")
    public DataSource dataSourceTest() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("schema.sql")
                .addScript("sql_data.sql").build();
    }

    @Bean
    public JdbcOperations jdbcTemplatesTest(DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "txManager")
    public PlatformTransactionManager transactionManager(DataSource ds) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(ds);
        return transactionManager;
    }
}
