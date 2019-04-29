create table IF NOT EXISTS configuration (
	id serial NOT NULL PRIMARY KEY,
	application VARCHAR(50),
	channel VARCHAR(50),
	configuration TEXT,
	create_date TIMESTAMP,
	update_date TIMESTAMP
);

create table IF not EXISTS configuration_audit (
    uid serial NOT NULL PRIMARY KEY,
	operation         char(1)   NOT NULL,
    stamp             timestamp NOT NULL,
    userid            VARCHAR(50)      NOT NULL,
	id INTEGER,
	application VARCHAR(50),
	channel VARCHAR(50),
	configuration TEXT,
	create_date TIMESTAMP,
	update_date TIMESTAMP
);


CREATE OR REPLACE FUNCTION process_configuration_audit() RETURNS TRIGGER AS '
    BEGIN
        --
        -- Create a row in emp_audit to reflect the operation performed on emp,
        -- make use of the special variable TG_OP to work out the operation.
        --
        IF (TG_OP = ''DELETE'') THEN
            INSERT INTO configuration_audit SELECT nextval(''configuration_audit_uid_seq''),''D'', now(), user, OLD.*;
            RETURN OLD;
        ELSIF (TG_OP = ''UPDATE'') THEN
            INSERT INTO configuration_audit SELECT nextval(''configuration_audit_uid_seq''),''U'', now(), user, NEW.*;
            RETURN NEW;
        ELSIF (TG_OP = ''INSERT'') THEN
            INSERT INTO configuration_audit SELECT nextval(''configuration_audit_uid_seq''),''I'', now(), user, NEW.*;
            RETURN NEW;
        END IF;
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
' LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS configuration_audit ON configuration;
CREATE TRIGGER  configuration_audit
AFTER INSERT OR UPDATE OR DELETE ON configuration
    FOR EACH ROW EXECUTE PROCEDURE process_configuration_audit();