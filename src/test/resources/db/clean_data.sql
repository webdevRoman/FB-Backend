DO
$$
    DECLARE
        record RECORD;
    BEGIN
        FOR record IN (SELECT tablename
                       FROM pg_tables
                       WHERE schemaname = current_schema()
                         AND tablename NOT IN ('databasechangelog', 'databasechangeloglock', 'shedlock', 'user_question'))
            LOOP
                EXECUTE 'TRUNCATE TABLE ' || record.tablename || ' CASCADE';
            END LOOP;
    END
$$;
