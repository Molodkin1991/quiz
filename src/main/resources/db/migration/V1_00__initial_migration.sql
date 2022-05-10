CREATE TABLE question(
  id INT NOT NULL AUTO_INCREMENT,
  question_content varchar(256),
  topic varchar(256),
  rank int
);

CREATE INDEX question_inx_topic on question(topic);

CREATE TABLE response(
  id INT NOT NULL AUTO_INCREMENT,
  response_content varchar(256),
  correct BOOLEAN default FALSE,
  question_id INT NOT NULL
);

ALTER TABLE response
    ADD FOREIGN KEY (question_id)
    REFERENCES question(id);


--todo add variable