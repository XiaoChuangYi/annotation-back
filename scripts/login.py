# coding=utf-8
import requests


def login(url, username, password):
    session = requests.session()

    session.post('%s/api/v2/user/login' % url, json={
        'accountName': username,
        'password': password
    })

    return session
