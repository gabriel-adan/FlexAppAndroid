CREATE TABLE Application (
    ViewKey TEXT NOT NULL,
    Type TEXT NOT NULL,
    Content TEXT NOT NULL,
    VersionId INTEGER REFERENCES Version (Id) NOT NULL
);