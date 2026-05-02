export const validators = {
  fullName: (value) => {
    if (!value?.trim()) return 'Full name is required';
    if (!/^[a-zA-Z ]{2,50}$/.test(value.trim()))
      return 'Name must contain only letters and spaces (2–50 characters)';
    return '';
  },
  email: (value) => {
    if (!value?.trim()) return 'Email is required';
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) return 'Enter a valid email address';
    return '';
  },
  password: (value) => {
    if (!value) return 'Password is required';
    if (value.length < 6) return 'Password must be at least 6 characters';
    return '';
  },
  phone: (value) => {
    if (!value?.trim()) return 'Phone number is required';
    if (!/^[+]?[0-9]{10,15}$/.test(value.trim()))
      return 'Enter a valid phone number (10–15 digits, optionally starting with +)';
    return '';
  },
  required: (label) => (value) => {
    if (!value?.toString().trim()) return `${label} is required`;
    return '';
  },
  minLength: (min) => (value) => {
    if (!value || value.length < min) return `Must be at least ${min} characters`;
    return '';
  },
  dueDate: (value) => {
    if (!value) return 'Due date is required';
    if (new Date(value) <= new Date()) return 'Due date must be in the future';
    return '';
  },
};

export const validateForm = (fields, rules) => {
  const errors = {};
  let valid = true;
  Object.keys(rules).forEach((key) => {
    const error = rules[key](fields[key]);
    if (error) { errors[key] = error; valid = false; }
  });
  return { errors, valid };
};
