# coding=utf-8
import json
import os
import io

import click
import requests


NAME_TITLE = '案例名：'
TEXT_TITLE = '案例细节：'


def parse_doc(doc):
    name = ''
    texts = []

    with io.open(doc, 'r', encoding='gbk') as f:
        for line in f:
            if not name:
                name = line.encode('utf-8').replace(NAME_TITLE, '')
            else:
                texts.append(line.encode('utf-8'))

    return {
        'name': name.strip(),
        'text': '\n'.join(texts).replace(TEXT_TITLE, '').strip()
    }


@click.command()
@click.argument('base_dir')
def run(base_dir):
    parsed_docs = map(
        parse_doc,
        filter(
            lambda f: os.path.isfile(f),
            map(
                lambda file_name: os.path.join(base_dir, file_name),
                os.listdir(base_dir)
            )
        )
    )

    requests.post('http://localhost:9001/api/v2/doc/import', json={
        'secretKey': '6Ha70Le63wyG2C14j2vMBpMi9qAHxT',
        'source': '杏树林',
        'data': parsed_docs
    })


if __name__ == '__main__':
    run()
