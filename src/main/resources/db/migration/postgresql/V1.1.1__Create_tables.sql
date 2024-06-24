
CREATE TABLE public.company (

  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  slug varchar(255) NOT NULL UNIQUE,
  name varchar(255) NOT NULL UNIQUE,
  official_name varchar(255) UNIQUE,
  state_tax_id varchar(255),
  federal_tax_id varchar(255) UNIQUE,
  phone varchar(255),
  email varchar(255),

  address_street varchar(255),
  address_street_number varchar(255),
  address_complement varchar(255),
  address_city_district varchar(255),
  address_post_code varchar(255),
  address_city varchar(255),
  address_state_code varchar(255),
  address_country varchar(255),
  address_latitude numeric(9, 7) CHECK (address_latitude BETWEEN -90 AND 90),
  address_longitude numeric(10, 7) CHECK (address_longitude BETWEEN -180 AND 180),

  is_management boolean DEFAULT FALSE,
  is_internal boolean DEFAULT FALSE,
  is_customer boolean DEFAULT FALSE,

  created_by varchar(255),
  updated_by varchar(255),

  created_at timestamp NOT NULL DEFAULT current_timestamp,
  updated_at timestamp NOT NULL DEFAULT current_timestamp
);
CREATE INDEX company_slug_idx ON company(slug);

CREATE TABLE public.api_key
(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id bigint NOT NULL REFERENCES company(id),
    name varchar(255) NOT NULL,
    key varchar(255) NOT NULL UNIQUE,
    is_active boolean NOT NULL DEFAULT FALSE,

    created_by varchar(255),
    updated_by varchar(255),

    created_at timestamp NOT NULL DEFAULT current_timestamp,
    updated_at timestamp NOT NULL DEFAULT current_timestamp
);
CREATE INDEX api_key_key_is_active_idx ON api_key(key, is_active);

INSERT INTO company (slug, name, email, is_management, created_at, updated_at)
VALUES ('my-company-management', 'My company mgmt', 'my-company-management@gmail.com', true, NOW(), NOW());
INSERT INTO api_key (company_id, name, key, is_active, created_at, updated_at)
VALUES (1, 'my company management', 'apikey-mgmt', true, NOW(), NOW());

INSERT INTO company (slug, name, email, is_internal, created_at, updated_at)
VALUES ('my-company-internal', 'My company internal', 'my-company-internal@gmail.com', true, NOW(), NOW());
INSERT INTO api_key (company_id, name, key, is_active, created_at, updated_at)
VALUES (2, 'my company internal', 'apikey-internal', true, NOW(), NOW());

INSERT INTO company (slug, name, email, is_customer, created_at, updated_at)
VALUES ('my-first-client', 'My first client', 'my-first-client@gmail.com', true, NOW(), NOW());
INSERT INTO api_key (company_id, name, key, is_active, created_at, updated_at)
VALUES (3, 'apikey My first client', 'apikey-client', true, NOW(), NOW());
