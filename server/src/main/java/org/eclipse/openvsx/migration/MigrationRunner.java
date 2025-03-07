/** ******************************************************************************
 * Copyright (c) 2022 Precies. Software Ltd and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * ****************************************************************************** */
package org.eclipse.openvsx.migration;

import org.eclipse.openvsx.repositories.RepositoryService;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.jobrunr.scheduling.JobRequestScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MigrationRunner implements JobRequestHandler<HandlerJobRequest<?>> {

    private final OrphanNamespaceMigration orphanNamespaceMigration;
    private final RepositoryService repositories;
    private final MigrationService migrations;
    private final JobRequestScheduler scheduler;

    @Value("${ovsx.data.mirror.enabled:false}")
    boolean mirrorEnabled;

    public MigrationRunner(
            OrphanNamespaceMigration orphanNamespaceMigration,
            RepositoryService repositories,
            MigrationService migrations,
            JobRequestScheduler scheduler
    ) {
        this.orphanNamespaceMigration = orphanNamespaceMigration;
        this.repositories = repositories;
        this.migrations = migrations;
        this.scheduler = scheduler;
    }

    @Override
    @Job(name = "Run migrations", retries = 0)
    public void run(HandlerJobRequest<?> jobRequest) throws Exception {
        orphanNamespaceMigration.fixOrphanNamespaces();
        setPreReleaseMigration();
        renameDownloadsMigration();
        extractVsixManifestMigration();
        fixTargetPlatformMigration();
        generateSha256ChecksumMigration();
        extensionVersionSignatureMigration();
        checkPotentiallyMaliciousExtensionVersions();
        migrateLocalNamespaceLogos();
        migrateLocalFileResourceContent();
        removeFileResourceTypeResource();
    }

    private void setPreReleaseMigration() {
        var jobName = "SetPreReleaseMigration";
        var handler = SetPreReleaseJobRequestHandler.class;
        repositories.findNotMigratedPreReleases().forEach(item -> migrations.enqueueMigration(jobName, handler, item));
    }

    private void renameDownloadsMigration() {
        var jobName = "RenameDownloadsMigration";
        var handler = RenameDownloadsJobRequestHandler.class;
        repositories.findNotMigratedRenamedDownloads().forEach(item -> migrations.enqueueMigration(jobName, handler, item));
    }

    private void extractVsixManifestMigration() {
        var jobName = "ExtractVsixManifestMigration";
        var handler = ExtractVsixManifestsJobRequestHandler.class;
        repositories.findNotMigratedVsixManifests().forEach(item -> migrations.enqueueMigration(jobName, handler, item));
    }

    private void fixTargetPlatformMigration() {
        var jobName = "FixTargetPlatformMigration";
        var handler = FixTargetPlatformsJobRequestHandler.class;
        repositories.findNotMigratedTargetPlatforms().forEach(item -> migrations.enqueueMigration(jobName, handler, item));
    }

    private void generateSha256ChecksumMigration() {
        var jobName = "GenerateSha256ChecksumMigration";
        var handler = GenerateSha256ChecksumJobRequestHandler.class;
        repositories.findNotMigratedSha256Checksums().forEach(item -> migrations.enqueueMigration(jobName, handler, item));
    }

    private void extensionVersionSignatureMigration() {
        if(!mirrorEnabled) {
            scheduler.enqueue(new HandlerJobRequest<>(GenerateKeyPairJobRequestHandler.class));
        }
    }

    private void checkPotentiallyMaliciousExtensionVersions() {
        var jobName = "CheckPotentiallyMaliciousExtensionVersions";
        var handler = PotentiallyMaliciousJobRequestHandler.class;
        repositories.findNotMigratedPotentiallyMalicious().forEach(item -> migrations.enqueueMigration(jobName, handler, item));
    }

    private void migrateLocalNamespaceLogos() {
        var jobName = "LocalNamespaceLogoMigration";
        var handler = NamespaceLogoFileResourceJobRequestHandler.class;
        repositories.findNotMigratedLocalNamespaceLogos().forEach(item -> migrations.enqueueMigration(jobName, handler, item));
    }

    private void migrateLocalFileResourceContent() {
        var jobName = "LocalFileResourceContentMigration";
        var handler = FileResourceContentJobRequestHandler.class;
        repositories.findNotMigratedLocalFileResourceContent().forEach(item -> migrations.enqueueMigration(jobName, handler, item));
    }

    private void removeFileResourceTypeResource() {
        var jobName = "RemoveFileResourceTypeResourceMigration";
        var handler = RemoveFileResourceTypeResourceJobRequestHandler.class;
        repositories.findNotMigratedFileResourceTypeResource().forEach(item -> migrations.enqueueMigration(jobName, handler, item));
    }
}
