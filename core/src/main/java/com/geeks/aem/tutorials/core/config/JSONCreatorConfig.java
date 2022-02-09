package com.geeks.aem.tutorials.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Geeks Tutorials - JSON Creator Scheduler Configuration",
        description = "Configuration for JSON Creator Scheduler. "
)
public @interface JSONCreatorConfig {

    @AttributeDefinition(
            name = "Scheduler name",
            description = "Name of the scheduler",
            type = AttributeType.STRING)
    public String schedulerName() default "GeeksTutorial_JSON_Creator";

    @AttributeDefinition(
            name = "Cron Expression",
            description = "Cron expression used by the scheduler",
            type = AttributeType.STRING)
    public String cronExpression() default "0 0 12 1/1 * ? *";


    @AttributeDefinition(
            name = "Skip Sites",
            description = "Add countries to exclude",
            type = AttributeType.STRING
    )
    public String[] getSkipSites() default {"language-masters"};

    @AttributeDefinition(
            name = "Site Path",
            description = "Add sites parent path",
            type = AttributeType.STRING
    )
    public String getSiteParent() default "/content/geekstutorials";
}

