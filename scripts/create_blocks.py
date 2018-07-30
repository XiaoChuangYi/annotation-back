import asyncio
import datetime

import aiohttp
import json

import click


async def get_docs(session, url, count, states):
    async with session.get('%s/api/v2/doc/list-doc' % url, params={'pageIndex': 1, 'pageSize': count, 'states': ','.join(states)}) as res:
        text = await res.text()
        return json.loads(text)['data']


async def create_blocks(session, url, docs, annotation_type):
    print('create blocks from docs(count: %s), annotation type: %s' % (len(docs), annotation_type))
    async with session.post('%s/api/v2/doc/create-blocks' % url, json={'docIds': list(map(lambda doc: doc['id'], docs)), 'annotationType': annotation_type}) as res:
        text = await res.text()
        try:
            result = json.loads(text)
            return result['data']['createdBlocks'] if result and result['data'] else 0
        except:
            print('error %s' % ','.join(list(map(lambda doc: str(doc['id']), docs))))
            return 0


async def main(states, count, annotation_type, url, username, password):
    async with aiohttp.ClientSession() as session:
        await session.post('%s/api/v2/user/login' % url, json={
            'accountName': username,
            'password': password
        })

        created_doc_count = 0
        while created_doc_count < count:
            page_size = min(10, count - created_doc_count)
            docs = await get_docs(session, url, page_size, states)
            if not (docs and docs['dataList']):
                continue
            print('%s: docs get, total: %s, current count: %s' % (datetime.datetime.now(), docs['total'], len(docs['dataList'])))
            if len(docs['dataList']) == 0:
                break

            tasks = []
            for i in range(0, len(docs['dataList']), 10):
                tasks.append(create_blocks(session, url, docs['dataList'][i:i+10], annotation_type))
            results = await asyncio.gather(*tasks)
            print('%s blocks created for docs: %s' % (sum(results), len(docs['dataList'])))
            created_doc_count += page_size


@click.command()
@click.argument('states', nargs=-1, type=str)
@click.option('--count', type=int, default=200, show_default=True)
@click.option('--annotation-type', type=int, default=2, show_default=True)
@click.option('--url', default='http://localhost:9001', show_default=True)
@click.option('--username', default='admin')
@click.option('--password', default='123456')
def run(states, count, annotation_type, url, username, password):
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main(states, count, annotation_type, url, username, password))
    # session = login(url, username, password)
    #
    # print('logged in, getting docs')
    #
    # docs = json.loads(session.get(
    #         '%s/api/v2/doc/list-doc' % url,
    #         params={
    #             'pageIndex': 1,
    #             'pageSize': count,
    #             'states': states,
    #         }
    # ).content)['data']
    #
    # print('docs get, total: %s, current count: %s' % (docs['total'], len(docs['dataList'])))
    #
    # print('create blocks from docs(count: %s), annotation type: %s' % (len(docs['dataList']), annotation_type))
    # result = session.post('%s/api/v2/doc/create-blocks' % url, json={
    #     'docIds': map(lambda doc: doc['id'], docs['dataList']),
    #     'annotationType': annotation_type
    # }).content
    # print('result: %s' % result)


if __name__ == '__main__':
    run()
