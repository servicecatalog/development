-- Revert renaming aws specific configsettings, remove the "AWS_" if existing, if not ignore.
UPDATE "configurationsetting" SET settingkey='ACCESS_KEY_ID_PWD' WHERE settingkey='AWS_ACCESS_KEY_ID_PWD';
UPDATE "configurationsetting" SET settingkey='SECRET_KEY_PWD' WHERE settingkey='AWS_SECRET_ACCESS_KEY_PWD';