# coding=utf-8
from login import login


if __name__ == '__main__':
    url = 'http://annotation.malgo.cn'
    session = login(url, 'test1', '123456')
    page_size = 20
    count = 0
    total = 0

    while True:
        res = session.get(f'{url}/api/v2/list-annotation?pageIndex=1&pageSize={page_size}&annotationTypes=2&states=PRE_ANNOTATION&states=ANNOTATION_PROCESSING').json()['data']
        if res['total'] == 0:
            break
        if not total:
            total = res['total']

        for annotation in res['dataList']:
            session.post(f'{url}/api/v2/commit-annotation', json={
                'id': annotation['id'],
                'autoAnnotation': ''
            })
            count += 1

        print(f'committed {count}/{total} annotations')
