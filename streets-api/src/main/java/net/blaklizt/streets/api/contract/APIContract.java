package net.blaklizt.streets.api.contract;

import net.blaklizt.symbiosis.sym_persistence.entity.super_class.symbiosis_entity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public interface APIContract <E extends symbiosis_entity, P extends Serializable, C extends APIContract> extends Serializable
{
    class MessageHeader<E extends symbiosis_entity, P extends Serializable, C extends APIContract> {

        @NotNull
        protected static Long deviceTransactionId;

        @NotNull
        protected static Long reportingTimeDelay;

        @NotNull
        protected static Integer attemptCount;

        @NotNull
        protected static String apiVersion;

        public static Long getDeviceTransactionId() { return deviceTransactionId; }

        public static void setDeviceTransactionId(Long deviceTransactionId) {
            MessageHeader.deviceTransactionId = deviceTransactionId;
        }

        public static void setDelay(Long reportingTimeDelay) {
            MessageHeader.reportingTimeDelay = reportingTimeDelay;
        }

        /* will be specified in milliseconds */
        public static Long getReportingTimeDelay() { return reportingTimeDelay; }

        public static void setReportingTimeDelay(Long reportingTimeDelay) {
            MessageHeader.reportingTimeDelay = reportingTimeDelay;
        }
        public static Integer getAttemptCount() {
            return attemptCount;
        }

        public static void setAttemptCount(Integer attemptCount) {
            MessageHeader.attemptCount = attemptCount;
        }

        public static String getApiVersion() {
            return apiVersion;
        }

        public static void setApiVersion(String apiVersion) {
            MessageHeader.apiVersion = apiVersion;
        }

        public static Long getReportingTimeDelayMinutes() { return (reportingTimeDelay / 1000) / 60; }

        public static Long getReportingTimeDelaySeconds() { return reportingTimeDelay / 1000; }
    }
}
