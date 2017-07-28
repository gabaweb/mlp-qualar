CREATE TABLE ENTRADAS (
    ID INTEGER PRIMARY KEY NOT NULL,
    ID_VARIAVEL INTEGER NOT NULL,
    VALOR REAL,
    HORARIO TEXT NOT NULL
);

CREATE TABLE ENTRADAS_TRATADAS (
    ID INTEGER PRIMARY KEY NOT NULL,
    ID_VARIAVEL INTEGER NOT NULL,
    VALOR REAL NOT NULL,
    HORARIO TEXT NOT NULL 
);

CREATE TABLE VARIAVEIS (
    ID INTEGER PRIMARY KEY NOT NULL,
    NOME TEXT NOT NULL
);