# coding=utf-8
import click

from login import login


@click.command()
@click.argument('block_id')
@click.option('--action', default='RE_EXAMINE', show_default=True)
@click.option('--url', default='http://annotation.malgo.cn', show_default=True)
@click.option('--username', default='admin')
@click.option('--password', default='123456')
def run(block_id, action, url, username, password):
    session = login(url, username, password)

    session.post('%s/api/v2/block/reset-block-to-annotation' % url, json={
        'ids': [block_id],
        'action': action
    })


if __name__ == '__main__':
    run()
