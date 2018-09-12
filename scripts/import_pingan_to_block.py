import json

import click
import pandas as pd
from sqlalchemy import Column, Integer, String, ForeignKey, Float
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, relationship

Base = declarative_base()


class AnnotationTaskBlock(Base):
    __tablename__ = 'annotation_task_block'

    id = Column(Integer, primary_key=True)
    annotation = Column(String, default='')
    annotation_type = Column(Integer)
    state = Column(String, default='CREATED')
    text = Column(String)
    ner_fresh_rate = Column(Float, default=0)
    assignee = Column(Integer, default=0)
    memo = Column(String)


def create_session(**kwargs):
    engine = create_engine('mysql+mysqlconnector://{username}:{password}@{host}/{db_name}'.format_map(kwargs), echo=False)
    return sessionmaker(bind=engine, autocommit=True)()


@click.command()
@click.option('--db-host', default='localhost', show_default=True)
@click.option('--db-user')
@click.option('--db-password')
@click.option('--db-name')
def main(db_host, db_user, db_password, db_name):
    session = create_session(username=db_user, password=db_password, host=db_host, db_name=db_name)
    df = pd.read_excel('./data/2016国标版疾病分类编码ICD-10(GBT14396-2016)维护版-平安扩展版.xls')

    with session.begin():
        for index, row in df.iterrows():
            icd: str = row[0]
            name: str = row[2]

            state = 'CREATED'

            if icd.startswith('U') or icd.startswith('V') or icd.startswith('W') or icd.startswith('X') or icd.startswith('Y') or icd.startswith('Z'):
                state = 'FINISHED'
            elif '后遗症' in name or '术后' in name or '伴' in name or '并' in name:
                state = 'FINISHED'

            session.add(AnnotationTaskBlock(annotation_type=3, text=name.strip(), state=state, memo=json.dumps({
                'icd': icd
            })))


if __name__ == '__main__':
    main()
