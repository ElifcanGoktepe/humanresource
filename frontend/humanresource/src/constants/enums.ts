// Backend enum değerlerine uygun frontend constants

export const Gender = {
  MALE: 'MALE',
  FEMALE: 'FEMALE'
} as const;

export const BloodType = {
  A_POSITIVE: 'A_POSITIVE',
  A_NEGATIVE: 'A_NEGATIVE',
  B_POSITIVE: 'B_POSITIVE',
  B_NEGATIVE: 'B_NEGATIVE',
  AB_POSITIVE: 'AB_POSITIVE',
  AB_NEGATIVE: 'AB_NEGATIVE',
  O_POSITIVE: 'O_POSITIVE',
  O_NEGATIVE: 'O_NEGATIVE'
} as const;

export const EducationLevel = {
  PRIMARY_SCHOOL: 'PRIMARY_SCHOOL',
  MIDDLE_SCHOOL: 'MIDDLE_SCHOOL',
  HIGH_SCHOOL: 'HIGH_SCHOOL',
  ASSOCIATE_DEGREE: 'ASSOCIATE_DEGREE',
  BACHELOR: 'BACHELOR',
  MASTER: 'MASTER',
  DOCTORATE: 'DOCTORATE'
} as const;

export const MaritalStatus = {
  SINGLE: 'SINGLE',
  MARRIED: 'MARRIED',
  DIVORCED: 'DIVORCED',
  WIDOWED: 'WIDOWED'
} as const;

// Type definitions for TypeScript
export type GenderType = typeof Gender[keyof typeof Gender];
export type BloodTypeType = typeof BloodType[keyof typeof BloodType];
export type EducationLevelType = typeof EducationLevel[keyof typeof EducationLevel];
export type MaritalStatusType = typeof MaritalStatus[keyof typeof MaritalStatus];

// Display labels for UI
export const GenderLabels = {
  [Gender.MALE]: 'Male',
  [Gender.FEMALE]: 'Female'
};

export const BloodTypeLabels = {
  [BloodType.A_POSITIVE]: 'A+',
  [BloodType.A_NEGATIVE]: 'A-',
  [BloodType.B_POSITIVE]: 'B+',
  [BloodType.B_NEGATIVE]: 'B-',
  [BloodType.AB_POSITIVE]: 'AB+',
  [BloodType.AB_NEGATIVE]: 'AB-',
  [BloodType.O_POSITIVE]: 'O+',
  [BloodType.O_NEGATIVE]: 'O-'
};

export const EducationLevelLabels = {
  [EducationLevel.PRIMARY_SCHOOL]: 'Primary School',
  [EducationLevel.MIDDLE_SCHOOL]: 'Middle School',
  [EducationLevel.HIGH_SCHOOL]: 'High School',
  [EducationLevel.ASSOCIATE_DEGREE]: 'Associate Degree',
  [EducationLevel.BACHELOR]: 'Bachelor\'s Degree',
  [EducationLevel.MASTER]: 'Master\'s Degree',
  [EducationLevel.DOCTORATE]: 'Doctorate'
};

export const MaritalStatusLabels = {
  [MaritalStatus.SINGLE]: 'Single',
  [MaritalStatus.MARRIED]: 'Married',
  [MaritalStatus.DIVORCED]: 'Divorced',
  [MaritalStatus.WIDOWED]: 'Widowed'
}; 