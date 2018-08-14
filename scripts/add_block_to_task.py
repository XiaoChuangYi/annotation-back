import click
from login import login

# 259845 259846 259847 259848 259849 259850 267793 267794 267795 267796 267797 267798 267799 267800 267801 83894 267100 267101 267102 267103 267104 267105 267106 267107 267108 267109 268014 268015 268016


@click.command()
@click.argument('block_ids', type=click.INT, nargs=-1)
@click.option('--task-id', type=click.INT)
@click.option('--url', default='http://test.annotation.malgo.cn', show_default=True)
@click.option('--username', default='admin')
@click.option('--password', default='123456')
def run(block_ids, task_id, url, username, password):
    session = login(url, username, password)

    print('logged in')
    print('adding blocks to doc(%s): %s' % (task_id, block_ids))

    print(session.post(
        '%s/api/v2/task/add-blocks-to-task' % url,
        json={
            'taskId': task_id,
            'blockIds': block_ids
        }
    ).content)


if __name__ == '__main__':
    run()
