import useToggle from '@/hooks/useToggle';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { LoginForm, ProFormCheckbox, ProFormText } from '@ant-design/pro-components';
import { Form } from 'antd';
import { message } from '@/components/StaticAnt';
import { create, EUserRole, ICreate, login } from '@/api/user';
import buildings from './buildings.webp'

export default () => {
  const [form] = Form.useForm();
  const [on, { toggle }] = useToggle();

  const handleClick = () => {
    toggle();
    form.resetFields();
  };

  const onFinish = async (values: ICreate) => {
    if (on) {
      const res = await login(values);
      await message.success(`Welcome back, ${res.name}`);
    } else {
      if (values.password !== values.password2) {
        await message.error('Passwords do not match');
        return;
      }
      await create(values);
      await message.success('User created');
      handleClick();
    }
  };
  return (
    <div className="h-screen relative bg-background"  style={{ background: `url(${buildings})` }}>
      <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-backgroundSecondary p-8 rounded shadow-md">
        <LoginForm<ICreate>
          onFinish={onFinish}
          form={form}
          submitter={{
            searchConfig: {
              submitText: on ? 'Sign In' : 'Register',
            },
          }}
        >
          <div className="text-center text-2xl font-bold mb-10">{on ? 'Sign In' : 'Register'}</div>
          <ProFormText
            name="username"
            fieldProps={{
              size: 'large',
              maxLength: 20,
              showCount: true,
              prefix: <UserOutlined className={'prefixIcon'} />,
            }}
            placeholder="Username"
            rules={[
              {
                required: true,
                whitespace: true,
                message: 'Username',
              },
              {
                pattern: /^[A-Za-z0-9]+$/,
                message: 'Only alphanumeric input allowed',
              },
            ]}
          />
          <ProFormText.Password
            name="password"
            fieldProps={{
              size: 'large',
              maxLength: 18,
              showCount: true,
              prefix: <LockOutlined className={'prefixIcon'} />,
            }}
            placeholder="Password"
            rules={[
              {
                required: true,
                message: 'Password',
              },
            ]}
          />
          {!on && (
            <ProFormText.Password
              name="password2"
              fieldProps={{
                size: 'large',
                maxLength: 18,
                showCount: true,
                prefix: <LockOutlined className={'prefixIcon'} />,
              }}
              placeholder="Password"
              rules={[
                {
                  required: true,
                  message: 'Password',
                },
              ]}
            />
          )}
          {on && (
            <ProFormCheckbox initialValue={true} noStyle name="remember_me">
              Remember login for 7 days
            </ProFormCheckbox>
          )}
        </LoginForm>
        {on ? (
          <span className="text-primaryLight hover:text-primary cursor-pointer" onClick={handleClick}>
            Register
          </span>
        ) : (
          <>
            <span className="mr-2">Already have an account?</span>
            <span className="text-primaryLight hover:text-primary cursor-pointer" onClick={handleClick}>
              Sign In
            </span>
          </>
        )}
      </div>
    </div>
  );
};
