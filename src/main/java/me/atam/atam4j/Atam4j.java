package me.atam.atam4j;

import io.dropwizard.setup.Environment;
import me.atam.atam4j.health.AcceptanceTestsHealthCheck;
import me.atam.atam4j.health.AcceptanceTestsState;

import java.util.concurrent.TimeUnit;

public class Atam4j {

    private Environment environment;
    private AcceptanceTestsRunnerTaskScheduler acceptanceTestsRunnerTaskScheduler;

    public Atam4j(AcceptanceTestsRunnerTaskScheduler acceptanceTestsRunnerTaskScheduler,
                  Environment environment) {
        this.acceptanceTestsRunnerTaskScheduler = acceptanceTestsRunnerTaskScheduler;
        this.environment = environment;
    }

    public void initialise() {
        acceptanceTestsRunnerTaskScheduler.scheduleAcceptanceTestsRunnerTask();
        AcceptanceTestsHealthCheck healthCheck = new AcceptanceTestsHealthCheck(acceptanceTestsRunnerTaskScheduler.getAcceptanceTestsState());
        environment.healthChecks().register(AcceptanceTestsHealthCheck.NAME, healthCheck);
    }

    public static class Atam4jBuilder {

        private Environment environment = null;
        private Class testClasses[] = null;
        private long initialDelay = 60;
        private long period = 300;
        private TimeUnit unit = TimeUnit.SECONDS;

        public Atam4jBuilder withEnvironment(Environment environment) {
            this.environment = environment;
            return this;
        }

        public Atam4jBuilder withTestClasses(Class[] testClasses) {
            this.testClasses = testClasses;
            return this;
        }

        public Atam4jBuilder withInitialDelay(long initialDelay) {
            this.initialDelay = initialDelay;
            return this;
        }

        public Atam4jBuilder withPeriod(long period) {
            this.period = period;
            return this;
        }

        public Atam4jBuilder withUnit(TimeUnit unit) {
            this.unit = unit;
            return this;
        }

        public Atam4j build() {

            if (environment == null) {
                throw new IllegalStateException("No Environment specified");
            }

            if (testClasses == null) {
                throw new IllegalStateException("No test classes specified");
            }

            return new Atam4j(
                    new AcceptanceTestsRunnerTaskScheduler(
                        environment,
                        testClasses,
                        new AcceptanceTestsState(),
                        initialDelay,
                        period,
                        unit),
                    this.environment);
        }
    }
}