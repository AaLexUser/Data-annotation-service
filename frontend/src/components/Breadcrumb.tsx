import type { BreadcrumbItemProps } from 'antd';
import { Breadcrumb as BreadcrumbAnt } from 'antd';
import type { Params } from 'react-router-dom';
import { useLocation, Link, useMatches } from 'react-router-dom';

export interface Match {
    id: string;
    pathname: string;
    params: Params<string>;
    data: unknown;
    handle: {
        crumb?: (data?: any) => React.ReactNode;
    };
}