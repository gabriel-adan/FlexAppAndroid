CREATE TABLE Service (
    Type TEXT NOT NULL,
    Content TEXT NOT NULL,
    VersionId INTEGER REFERENCES Version (Id) NOT NULL
);