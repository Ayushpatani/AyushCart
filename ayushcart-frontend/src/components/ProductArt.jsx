// Products have no photos by default, so each one gets a generated tile:
// its category colour with the product's initials set large and cropped by the edge.

const CATEGORY_COLOURS = {
  Electronics: '#34569A',
  Fashion: '#A8406F',
  'Home & Kitchen': '#2B7468',
  Books: '#86561C',
  Sports: '#48782A',
};

function colourFor(category = '') {
  if (CATEGORY_COLOURS[category]) return CATEGORY_COLOURS[category];
  let hash = 0;
  for (const ch of category) hash = (hash * 31 + ch.charCodeAt(0)) % 360;
  return `hsl(${hash} 42% 36%)`;
}

function initials(name = '') {
  return name
    .split(/\s+/)
    .filter((w) => /[a-z0-9]/i.test(w[0] ?? ''))
    .slice(0, 2)
    .map((w) => w[0])
    .join('')
    .toUpperCase();
}

export default function ProductArt({ name, category, imageUrl, size = 'card' }) {
  if (imageUrl) {
    return (
      <div className={`art art-${size} art-photo`}>
        <img src={imageUrl} alt="" loading="lazy" />
      </div>
    );
  }
  return (
    <div className={`art art-${size}`} style={{ '--art': colourFor(category) }} aria-hidden="true">
      <span className="art-letters">{initials(name)}</span>
    </div>
  );
}
