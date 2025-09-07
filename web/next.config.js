/** @type {import('next').NextConfig} */
const nextConfig = {
  experimental: {
    appDir: true,
  },
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'https://us-central1-YOUR_PROJECT_ID.cloudfunctions.net/:path*',
      },
    ]
  },
}

module.exports = nextConfig
