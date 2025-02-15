import { useMatches } from 'react-router-dom';
import { useEffect, useState } from 'react';
import _ from 'lodash';
import type { Match } from '@/components/Breadcrumb';
import { DatabaseOutlined, TeamOutlined } from '@ant-design/icons';
import { ProLayout, PageContainer } from '@ant-design/pro-components';
import { hasPermission } from '../constants/access';
import LabelingTitle from '../assets/DeepPick.svg';
import { logout, IUserInfo } from '@/api/user';
import { goLogin } from '@/utils/sso';
import logo from '../assets/logo.svg';
import clsx from 'clsx';
import { Avatar, Dropdown, MenuProps } from 'antd';
import { ImportOutlined } from '@ant-design/icons';

export default () => {
  const [collapse, setCollapse] = useState(false);
  const matches = useMatches() as Match[];
  const title = _.chain(matches)
    .filter((match) => Boolean(match.handle?.crumb))
    .map((match) => match.handle.crumb!(match.data))
    .last()
    .value() as string;

  const onLogout = async () => {
    await logout();
    goLogin();
  };

  const name = 'Admin';
  const items: MenuProps['items'] = [
    {
      key: 'quit',
      icon: <ImportOutlined className="text-icon" />,
      label: <a onClick={onLogout}>Logout</a>,
    },
  ];

  const route = {
    path: '/',
    children: [
      {
        path: '/task',
        name: 'Batch Management',
        icon: <DatabaseOutlined />,
      },
      {
        path: '/users',
        name: 'User Management',
        hide: false,
        icon: <TeamOutlined />,
        children: [
          {
            path: 'assessors',
            name: 'Assessors',
          },
          {
            path: 'admins',
            name: 'Admins',
          },
        ],
      },
    ].filter((item) => !item.hide),
  };

  const isRead = true;
  const defaultProperty = isRead
    ? {
        route,
      }
    : {
        defaultCollapsed: true,
        collapsedButtonRender: () => false,
        route: undefined,
      };

  return (
    <ProLayout
      // @ts-ignore
      title={<div className="ml-2 -mt-1 text-primary text-2xl">DeepPick</div>}
      // @ts-ignore
      logo={<img src={logo} className='scale-150'/>}
      collapsed={collapse}
      onCollapse={setCollapse}
      pageTitleRender={() => title}
      {...defaultProperty}
      menu={{ autoClose: false, defaultOpenAll: true }}
      avatarProps={{
        render: (p) => {
          return (
            <Dropdown placement="topLeft" menu={{ items }}>
              <div className={clsx('flex items-center gap-x-1 cursor-pointer', !collapse && 'pl-2')}>
                <Avatar className="bg-primary">{name[0]}</Avatar>
                {!collapse && <span className="text-color">{name}</span>}
              </div>
            </Dropdown>
          );
        },
      }}
    ></ProLayout>
  );
};
