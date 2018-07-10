# coding=utf-8
import json

import click
from login import login


@click.command()
@click.argument('task_name')
@click.argument('text_filter')
@click.option('--no-create-task', default=False, is_flag=True)
@click.option('--count', default='200', show_default=True)
@click.option('--min-text-length', default='500', show_default=True)
@click.option('--annotation-type', default='0', show_default=True)
@click.option('--url', default='http://localhost:9001', show_default=True)
@click.option('--username', default='admin')
@click.option('--password', default='123456')
def run(task_name, text_filter, no_create_task, count, min_text_length, annotation_type, url, username, password):
    session = login(url, username, password)

    print('logged in, getting docs')

    docs = json.loads(session.get(
        '%s/api/v2/doc/list-doc' % url,
        params={
            'pageIndex': 1,
            'pageSize': count,
            'name': text_filter,
            'minTextLength': min_text_length,
        }
    ).content)['data']

    print('docs get, total: %s, current count: %s' % (docs['total'], len(docs['dataList'])))

    if not no_create_task:
        print('creating task with name %s' % task_name)

        task = json.loads(session.post('%s/api/v2/task/create' % url, json={
            'name': task_name
        }).content)['data']

        print 'task created, id: %s' % task['id']
    else:
        # TODO get task detail with name
        task = None

    print('add docs(count: %s) to task, id: %s, annotation type: %s' % (len(docs['dataList']), task['id'], annotation_type))
    result = session.post('%s/api/v2/task/add-docs' % url, json={
        'id': task['id'],
        'docIds': map(lambda doc: doc['id'], docs['dataList']),
        'annotationType': annotation_type
    }).content
    print 'result: %s' % result


if __name__ == '__main__':
    run()
