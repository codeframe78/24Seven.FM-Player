(function () {
  'use strict';

  const root = document.documentElement;
  const header = document.querySelector('[data-site-header]');
  const navigation = document.querySelector('#project-navigation');
  const navToggle = document.querySelector('.nav-toggle');
  const themeToggle = document.querySelector('.theme-toggle');
  const themeLabel = document.querySelector('.theme-toggle-label');
  const themeColor = document.querySelector('meta[name="theme-color"]');
  const progress = document.querySelector('.scroll-progress span');
  const backToTop = document.querySelector('.back-to-top');
  const toast = document.querySelector('.site-toast');
  const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  function updateHeaderHeight() {
    if (header) root.style.setProperty('--header-height', `${header.offsetHeight}px`);
  }

  function setTheme(theme, persist) {
    root.dataset.theme = theme;
    const nextTheme = theme === 'dark' ? 'light' : 'dark';
    if (themeLabel) themeLabel.textContent = theme === 'dark' ? 'Dark' : 'Light';
    if (themeToggle) themeToggle.setAttribute('aria-label', `Switch to ${nextTheme} theme`);
    if (themeColor) themeColor.setAttribute('content', theme === 'dark' ? '#090c15' : '#f5f7fb');
    if (persist) {
      try { localStorage.setItem('project-theme', theme); } catch (error) { /* Preference storage is optional. */ }
    }
  }

  setTheme(root.dataset.theme === 'light' ? 'light' : 'dark', false);
  themeToggle?.addEventListener('click', function () {
    setTheme(root.dataset.theme === 'dark' ? 'light' : 'dark', true);
  });

  function closeNavigation() {
    if (!header || !navToggle) return;
    header.dataset.navOpen = 'false';
    navToggle.setAttribute('aria-expanded', 'false');
  }

  navToggle?.addEventListener('click', function () {
    if (!header) return;
    const isOpen = header.dataset.navOpen === 'true';
    header.dataset.navOpen = String(!isOpen);
    navToggle.setAttribute('aria-expanded', String(!isOpen));
    updateHeaderHeight();
  });
  navigation?.addEventListener('click', function (event) {
    if (event.target.closest('a')) closeNavigation();
  });
  document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') closeNavigation();
  });
  document.addEventListener('click', function (event) {
    if (header?.dataset.navOpen === 'true' && !header.contains(event.target)) closeNavigation();
  });

  let scrollFrame = null;
  function updateScrollState() {
    scrollFrame = null;
    const scrollable = document.documentElement.scrollHeight - window.innerHeight;
    const ratio = scrollable > 0 ? Math.min(1, window.scrollY / scrollable) : 0;
    if (progress) progress.style.transform = `scaleX(${ratio})`;
    if (backToTop) backToTop.dataset.visible = String(window.scrollY > 720);
  }
  window.addEventListener('scroll', function () {
    if (scrollFrame === null) scrollFrame = window.requestAnimationFrame(updateScrollState);
  }, { passive: true });
  backToTop?.addEventListener('click', function () {
    window.scrollTo({ top: 0, behavior: reducedMotion ? 'auto' : 'smooth' });
  });

  const jumpLinks = Array.from(document.querySelectorAll('.section-jump a[href^="#"]'));
  const observedSections = jumpLinks
    .map(function (link) { return document.querySelector(link.getAttribute('href')); })
    .filter(Boolean);
  if ('IntersectionObserver' in window && observedSections.length) {
    const sectionObserver = new IntersectionObserver(function (entries) {
      const visible = entries
        .filter(function (entry) { return entry.isIntersecting; })
        .sort(function (left, right) { return right.intersectionRatio - left.intersectionRatio; })[0];
      if (!visible) return;
      jumpLinks.forEach(function (link) {
        if (link.getAttribute('href') === `#${visible.target.id}`) link.setAttribute('aria-current', 'location');
        else link.removeAttribute('aria-current');
      });
    }, { rootMargin: '-30% 0px -58% 0px', threshold: [0, 0.2, 0.6] });
    observedSections.forEach(function (section) { sectionObserver.observe(section); });
  }

  function announce(message) {
    if (!toast) return;
    toast.textContent = message;
    toast.dataset.visible = 'true';
    window.clearTimeout(announce.timeout);
    announce.timeout = window.setTimeout(function () { toast.dataset.visible = 'false'; }, 1800);
  }

  function copyText(value) {
    if (navigator.clipboard && window.isSecureContext) return navigator.clipboard.writeText(value);
    const field = document.createElement('textarea');
    field.value = value;
    field.setAttribute('readonly', '');
    field.style.position = 'fixed';
    field.style.opacity = '0';
    document.body.appendChild(field);
    field.select();
    document.execCommand('copy');
    field.remove();
    return Promise.resolve();
  }

  document.querySelectorAll('main section[id]').forEach(function (section) {
    const heading = section.querySelector(':scope > .content-intro h2, :scope > .section-heading h2, :scope > .roadmap-callout > div:first-child h2');
    if (!heading) return;
    const button = document.createElement('button');
    button.className = 'anchor-copy';
    button.type = 'button';
    button.title = 'Copy link to this section';
    button.setAttribute('aria-label', `Copy link to ${heading.textContent.trim()}`);
    button.innerHTML = '<span aria-hidden="true">#</span>';
    button.addEventListener('click', function () {
      const url = new URL(window.location.href);
      url.hash = section.id;
      copyText(url.toString()).then(function () { announce('Section link copied'); });
    });
    heading.appendChild(button);
  });

  document.querySelectorAll('[data-filter-controls]').forEach(function (controls) {
    const target = document.querySelector(controls.dataset.target);
    if (!target) return;
    const items = Array.from(target.querySelectorAll('[data-filter-item]'));
    const queryField = controls.querySelector('[data-filter-query]');
    const stateButtons = Array.from(controls.querySelectorAll('[data-filter-state]'));
    const count = controls.querySelector('[data-filter-count]');
    const empty = target.querySelector('[data-filter-empty]');
    let selectedState = 'all';

    function applyFilter() {
      const query = (queryField?.value || '').trim().toLocaleLowerCase();
      let visibleCount = 0;
      items.forEach(function (item) {
        const matchesQuery = !query || item.textContent.toLocaleLowerCase().includes(query);
        const matchesState = selectedState === 'all' || item.dataset.state === selectedState;
        item.hidden = !(matchesQuery && matchesState);
        if (!item.hidden) visibleCount += 1;
      });
      if (count) count.textContent = String(visibleCount);
      if (empty) empty.hidden = visibleCount !== 0;
    }

    queryField?.addEventListener('input', applyFilter);
    stateButtons.forEach(function (button) {
      button.addEventListener('click', function () {
        selectedState = button.dataset.filterState;
        stateButtons.forEach(function (candidate) {
          candidate.setAttribute('aria-pressed', String(candidate === button));
        });
        applyFilter();
      });
    });
    applyFilter();
  });

  if (!reducedMotion && 'IntersectionObserver' in window) {
    const revealTargets = document.querySelectorAll('.feature-grid article, .portal-grid a, .roadmap-card, .detail-grid article, .principle-grid article, .evidence-grid article, .test-session-grid article, .milestone-history li, figure');
    root.classList.add('reveal-enabled');
    const revealObserver = new IntersectionObserver(function (entries, observer) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        entry.target.dataset.revealed = 'true';
        observer.unobserve(entry.target);
      });
    }, { rootMargin: '0px 0px -7% 0px', threshold: 0.08 });
    revealTargets.forEach(function (item) { revealObserver.observe(item); });
  }

  const mediaDialog = document.querySelector('.media-dialog');
  const mediaImage = mediaDialog?.querySelector('img');
  const mediaCaption = mediaDialog?.querySelector('p');
  const mediaClose = mediaDialog?.querySelector('.media-dialog-close');
  document.querySelectorAll('[data-lightbox]').forEach(function (trigger) {
    trigger.addEventListener('click', function () {
      if (!mediaDialog || !mediaImage || !mediaCaption) return;
      mediaImage.src = trigger.dataset.lightbox;
      mediaImage.alt = trigger.dataset.lightboxAlt || '';
      mediaCaption.textContent = trigger.dataset.lightboxCaption || '';
      mediaDialog.showModal();
    });
  });
  mediaClose?.addEventListener('click', function () { mediaDialog.close(); });
  mediaDialog?.addEventListener('click', function (event) {
    if (event.target === mediaDialog) mediaDialog.close();
  });

  window.addEventListener('resize', updateHeaderHeight, { passive: true });
  updateHeaderHeight();
  updateScrollState();
}());
