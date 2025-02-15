import shell from 'shelljs'
import inquirer from 'inquirer';

const options = [
  {
    type: 'list',
    name: 'command',
    message: 'Please select a command to execute',
    choices: [
      {
        key: 0,
        name: 'Start Login Page (login)',
        value: 'start:login',
      },
      {
        key: 1,
        name: 'Start Supplier Portal (supplier)',
        value: 'start:supplier',
      },
      {
        key: 2,
        name: 'Start Operator Portal (operator)',
        value: 'start:operator',
      },
      {
        key: 3,
        name: 'Build Supplier Portal (supplier)',
        value: 'build:supplier',
      },
      {
        key: 4,
        name: 'Build Operator Portal (operator)',
        value: 'build:operator',
      },
      {
        key: 5,
        name: 'Build All',
        value: 'build',
      },
    ],
  },
];

function main() {
  inquirer.prompt(options).then((answers) => {
    shell.exec(`pnpm ${answers.command}`);
  });
}

main();
