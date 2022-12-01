package com.osttra.fx.blockstream.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Gatewaybos.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {}
