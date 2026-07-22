(function () {
  'use strict';

  document.documentElement.classList.add('js');

  try {
    var savedTheme = localStorage.getItem('project-theme');
    var preferredTheme = window.matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'dark';
    document.documentElement.dataset.theme = savedTheme || preferredTheme;
  } catch (error) {
    document.documentElement.dataset.theme = 'dark';
  }
}());
