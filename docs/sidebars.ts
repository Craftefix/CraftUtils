import type {SidebarsConfig} from '@docusaurus/plugin-content-docs';

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */
const sidebars: SidebarsConfig = {
  tutorialSidebar: [
    'intro',
    'getting-started',
    {
      type: 'category',
      label: 'Commands',
      collapsed: false,
      items: [
        'commands/overview',
        'commands/player-abilities',
        'commands/teleportation',
        'commands/utilities',
        'commands/messaging',
        'commands/gamemode',
        'commands/gui',
        'commands/moderation',
      ],
    },
    {
      type: 'category',
      label: 'Configuration',
      collapsed: false,
      items: [
        'configuration/basic-setup',
        'configuration/database',
        'configuration/discord',
        'configuration/permissions',
        'configuration/languages',
      ],
    },
    {
      type: 'category',
      label: 'Support',
      collapsed: false,
      items: [
        'support/troubleshooting',
        'support/faq',
      ],
    },
  ],
};

export default sidebars;
